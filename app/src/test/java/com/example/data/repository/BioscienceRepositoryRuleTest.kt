package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.Project
import com.example.data.model.Protocol
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class BioscienceRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun createProject_authenticatedUser_success() = runBlocking {
        val uid = signInTestUser("researcher@bionexus.test")
        val repository = BioscienceRepository(firestore)

        val project = Project(
            userId = uid,
            title = "CRISPR-Cas9 Base Editing Assay",
            category = "Genomics",
            summary = "High-precision single-nucleotide cytosine conversion study.",
            content = "Experimental guide RNA and base editor expression parameters.",
            tags = listOf("CRISPR", "BaseEditing", "Genomics")
        )

        val docId = withTimeout(5000L) {
            repository.saveProject(project)
        }

        assertNotNull(docId)
        assertTrue(docId.isNotBlank())
    }

    @Test
    fun createProtocol_authenticatedUser_success() = runBlocking {
        val uid = signInTestUser("biochemist@bionexus.test")
        val repository = BioscienceRepository(firestore)

        val protocol = Protocol(
            userId = uid,
            name = "Protein Purification with Ni-NTA Column",
            category = "Proteomics",
            reagents = listOf("Lysis Buffer", "Imidazole", "Ni-NTA Agarose", "PBS"),
            steps = listOf("Harvest cell pellet", "Sonicate on ice", "Apply supernatant to column", "Elute with 250mM imidazole"),
            safetyNotes = "Operate in cold room at 4°C with standard PPE.",
            status = "Active"
        )

        val docId = withTimeout(5000L) {
            repository.saveProtocol(protocol)
        }

        assertNotNull(docId)
        assertTrue(docId.isNotBlank())
    }

    @Test
    fun saveProject_unauthenticatedUser_fails() = runBlocking {
        auth.signOut()
        val repository = BioscienceRepository(firestore)

        val project = Project(
            userId = "unauthenticated",
            title = "Unauthorized Project",
            category = "Genomics",
            summary = "Should fail fast",
            content = "Forbidden"
        )

        try {
            repository.saveProject(project)
            fail("Expected IllegalStateException for unauthenticated user")
        } catch (e: IllegalStateException) {
            assertTrue(e.message!!.contains("User must be signed in"))
        }
    }
}
