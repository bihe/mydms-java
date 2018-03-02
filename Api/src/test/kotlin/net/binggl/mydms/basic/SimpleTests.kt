package net.binggl.mydms.basic

import net.binggl.mydms.features.records.entity.SenderEntity
import org.junit.Assert
import org.junit.Test

class SimpleTests {

    @Test
    fun testMapAndJoin(): Unit {
        val senderList = listOf(SenderEntity("sender1"), SenderEntity("sender2"))
        val senderListJoined = senderList.map { it.name }.joinToString(";")

        Assert.assertEquals("sender1;sender2", senderListJoined)
    }
}