package com.example.njttracker.common.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class Departure(
    val trainId: String,
    val lineCode: String,
    val lineName: String,
    val lineColor: String,
    val destination: String,
    val trackConfidence: TrackConfidence,
    val track: String?,
    val scheduledTime: Long,
    val time: Long,
    val delay: Int,
    val status: TrainStatus,
    val occupancy: Float?,
    val cars: List<TrainCar>,
)

@Serializable
data class TrainCar(
    val carNo: String,
    val position: Int,
    val occupancy: Float,
)

@Serializable(with = TrackConfidenceSerializer::class)
enum class TrackConfidence(val serverValue: String) {
    NONE("none"), LOW("low"), MEDIUM("medium"), HIGH("high"), CONFIRMED("confirmed");
}
@Serializable(with = TrainStatusSerializer::class)
enum class TrainStatus(val serverValue: String) {
    ON_TIME("ON_TIME"),
    DELAYED("DELAYED"),
    CANCELLED("CANCELLED"),
    DEPARTED("DEPARTED")
}

@Serializable
data class Line(
    val lineCode: String,
    val lineAbbreviation: String,
    val lineName: String,
    val lineColor: String,
)

@Serializable
data class Station(
    val stationCode: String,
    val stationName: String,
    val stationFull: String,
    val wheelchairAccessible: Boolean,
    val latitude: Double?,
    val longitude: Double?,
    val lines: List<Line>,
    val nextDeparture: Departure?,
)

@Serializable
data class StationDetails(val station: Station, val departures: List<Departure>)

@Serializable
data class TrainStop(
    val stationCode: String,
    val stationName: String,
    val arrivalTime: Long,
    val departureTime: Long,
)

object TrackConfidenceSerializer : KSerializer<TrackConfidence> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "com.example.njttracker.common.model.TrackConfidence", PrimitiveKind.STRING
    )

    override fun serialize(encoder: Encoder, value: TrackConfidence) {
        encoder.encodeString(value.serverValue)
    }

    override fun deserialize(decoder: Decoder): TrackConfidence {
        val value = decoder.decodeString()
        return TrackConfidence.entries.find { it.serverValue == value } ?: TrackConfidence.NONE
    }
}

object TrainStatusSerializer : KSerializer<TrainStatus> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "com.example.njttracker.common.model.TrainStatus", PrimitiveKind.STRING
    )

    override fun serialize(encoder: Encoder, value: TrainStatus) {
        encoder.encodeString(value.serverValue)
    }

    override fun deserialize(decoder: Decoder): TrainStatus {
        val value = decoder.decodeString()
        return TrainStatus.entries.find { it.serverValue == value } ?: TrainStatus.ON_TIME
    }
}
