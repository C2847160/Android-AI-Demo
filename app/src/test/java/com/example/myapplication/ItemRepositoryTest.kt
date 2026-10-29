package com.example.myapplication

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ItemRepositoryTest {

    private lateinit var repository: ItemRepository

    @Before
    fun setUp() {
        repository = FakeItemRepository()
    }

    @Test
    fun getItems_returnsAllSampleItems() = runBlocking {
        val items = repository.getItems()

        assertEquals(5, items.size)
        assertEquals(FakeItemRepository.sampleItems, items)
    }

    @Test
    fun getItems_itemsHaveUniqueIds() = runBlocking {
        val items = repository.getItems()

        val ids = items.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun getItems_fieldsAreNotBlank() = runBlocking {
        val items = repository.getItems()

        items.forEach { item ->
            assertTrue(item.title.isNotBlank())
            assertTrue(item.description.isNotBlank())
        }
    }

    @Test
    fun getItem_existingId_returnsMatchingItem() = runBlocking {
        val item = repository.getItem(1)

        assertNotNull(item)
        assertEquals(1, item!!.id)
        assertEquals("Jetpack Compose", item.title)
    }

    @Test
    fun getItem_eachListedId_returnsItem() = runBlocking {
        val items = repository.getItems()

        items.forEach { expected ->
            val actual = repository.getItem(expected.id)
            assertEquals(expected, actual)
        }
    }

    @Test
    fun getItem_unknownId_returnsNull() = runBlocking {
        assertNull(repository.getItem(0))
        assertNull(repository.getItem(-1))
        assertNull(repository.getItem(999))
    }

    @Test
    fun getItem_intBoundaryValues_returnsNull() = runBlocking {
        assertNull(repository.getItem(Int.MAX_VALUE))
        assertNull(repository.getItem(Int.MIN_VALUE))
    }

    @Test
    fun getItem_idJustAboveLargest_returnsNull() = runBlocking {
        val maxId = repository.getItems().maxOf { it.id }

        assertNull(repository.getItem(maxId + 1))
    }

    @Test
    fun getItems_idsArePositive() = runBlocking {
        val items = repository.getItems()

        items.forEach { item ->
            assertTrue("id 应为正数，实际为 ${item.id}", item.id > 0)
        }
    }

    @Test
    fun getItems_calledTwice_returnsConsistentResults() = runBlocking {
        val first = repository.getItems()
        val second = repository.getItems()

        assertEquals(first, second)
        assertEquals(first.size, second.size)
    }

    @Test
    fun getItem_calledTwiceForSameId_returnsEqualItems() = runBlocking {
        val first = repository.getItem(3)
        val second = repository.getItem(3)

        assertNotNull(first)
        assertEquals(first, second)
    }

    @Test
    fun getItems_descriptionsAreReasonablyShort() = runBlocking {
        val items = repository.getItems()

        items.forEach { item ->
            assertTrue(
                "描述过长（${item.description.length} 字符）: ${item.title}",
                item.description.length <= 200
            )
        }
    }

    @Test
    fun getItem_returnsItemWhoseIdMatchesRequest() = runBlocking {
        val requestedId = 4

        val item = repository.getItem(requestedId)

        assertNotNull(item)
        assertEquals(requestedId, item!!.id)
    }
}
