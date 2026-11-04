package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class Project(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "",
    val category: String = "",
    val summary: String = "",
    val content: String = "",
    val tags: List<String> = emptyList(),
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Protocol(
    @DocumentId val id: String = "",
    val userId: String = "",
    val name: String = "",
    val category: String = "",
    val reagents: List<String> = emptyList(),
    val steps: List<String> = emptyList(),
    val safetyNotes: String = "",
    val status: String = "Active", // "Draft", "Active", "Completed"
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class ChatSession(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "",
    val modelUsed: String = "gemini-3.8-flash",
    val role: String = "Molecular Geneticist",
    val lastMessage: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

data class ChatMessage(
    val id: String = "",
    val sender: String = "user", // "user" or "model"
    val text: String = "",
    val modelTag: String = "",
    val thinkingProcess: String? = null,
    val groundingSource: String? = null,
    val timestampMs: Long = System.currentTimeMillis()
)

data class SequenceAnalysisResult(
    val sequenceLength: Int = 0,
    val gcContent: Double = 0.0,
    val atContent: Double = 0.0,
    val translatedAminoAcids: String = "",
    val openReadingFramesCount: Int = 0,
    val crisprPamSitesCount: Int = 0,
    val meltingTempCelsius: Double = 0.0,
    val aiInsight: String = ""
)

data class LiteratureItem(
    val title: String,
    val sourceOrJournal: String,
    val year: String,
    val abstractText: String,
    val doiOrUrl: String,
    val tags: List<String>
)

data class BiotechLocationItem(
    val name: String,
    val type: String, // "Sequencing Core", "Research Institute", "Biotech Incubator", "Clinical Center"
    val addressOrCity: String,
    val description: String,
    val specialties: List<String>
)
