package com.tmmtmm.sdk.db

import androidx.lifecycle.LifecycleOwner
import androidx.room.*
import com.tmmtmm.sdk.constant.ConversationIntroduceConstant
import com.tmmtmm.sdk.core.db.DataBaseManager
import com.tmmtmm.sdk.core.event.EventCenter
import com.tmmtmm.sdk.db.event.ConversationEvent
import com.tmmtmm.sdk.db.model.ConversationLinkModel
import com.tmmtmm.sdk.db.model.ConversationModel
import com.tmmtmm.sdk.db.result.ConversationInfoResult

/**
 * @description
 * @version
 */
class ConversationDbManager private constructor() {

    companion object {
        val INSTANCE: ConversationDbManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            ConversationDbManager()
        }
    }

    fun addConversationCallback(
        lifecycleOwner: LifecycleOwner?,
        callback: ConversationEvent.ConversationListener
    ) =
        EventCenter.handle<ConversationEvent>(lifecycleOwner)
            .addCallback(ConversationEvent().observe(callback))

    fun removeConversationCallback(
        lifecycleOwner: LifecycleOwner?,
    ) =
        EventCenter.handle<ConversationEvent>(lifecycleOwner)
            .removeCallback()


    fun loadMoreConversations(timeStamp: Long?, count: Int): MutableList<ConversationModel>? {
        val conversationEntities = DataBaseManager.getInstance().getDataBase()
            ?.conversationDao()
            ?.loadMoreConversations(timeStamp, count)

//        return handlerConversations(conversationEntities)
        return conversationEntities
    }

    fun queryConversations(): MutableList<ConversationModel>? {
        val conversationEntities = DataBaseManager.getInstance().getDataBase()
            ?.conversationDao()
            ?.queryConversations()

//        return handlerConversations(conversationEntities)
        return conversationEntities
    }

    fun queryRawConversations(): MutableList<ConversationModel>? {

        return DataBaseManager.getInstance().getDataBase()
            ?.conversationDao()
            ?.queryConversations()
    }


    fun queryTopConversations(): MutableList<ConversationModel>? {
        val conversationEntities = DataBaseManager.getInstance().getDataBase()
            ?.conversationDao()
            ?.queryAllTopConversations()

//        return handlerConversations(conversationEntities)
        return conversationEntities
    }


    fun queryConversations(chatIds: MutableSet<String>?): MutableList<ConversationModel>? {
        val conversationEntities = DataBaseManager.getInstance().getDataBase()
            ?.conversationDao()
            ?.queryConversationList(chatIds)

//        return handlerConversations(conversationEntities)
        return conversationEntities
    }

    fun queryRawConversations(chatIds: MutableSet<String>?): MutableList<ConversationModel>? {
        val conversationEntities = DataBaseManager.getInstance().getDataBase()
            ?.conversationDao()
            ?.queryConversationList(chatIds)

        return conversationEntities
    }


    fun insertGroupConversation(conversationModel: ConversationModel?){
        DataBaseManager.getInstance().getDataBase()
            ?.conversationDao()
            ?.insertGroupConversation(conversationModel)
    }



}

@Dao
interface ConversationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGroupConversation(conversationModel: ConversationModel?)

    @Update(entity = ConversationModel::class)
    fun updateConversationInfoList(conversationResults: MutableList<ConversationInfoResult>?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGroupConversations(conversationEntities: MutableList<ConversationModel>?)

    @Query("SELECT * FROM tmm_conversation where timeStamp < :timeStamp and topTime = 0 or topTime is null ORDER BY timeStamp DESC LIMIT :count")
    fun loadMoreConversations(timeStamp: Long?, count: Int): MutableList<ConversationModel>?

    @Query("select * from tmm_conversation")
    fun queryConversations(): MutableList<ConversationModel>?

    @Query("select * from tmm_conversation where topTime != 0")
    fun queryAllTopConversations(): MutableList<ConversationModel>?

    @Query("select * from tmm_conversation where chatId in (:chatIds) ORDER BY timeStamp desc")
    fun queryConversationList(chatIds: MutableSet<String>?): MutableList<ConversationModel>?


    @Query("update tmm_conversation set isTop = :isTop, topTime = :topTime Where chatId = :chatId")
    fun updateTop(chatId: String?, isTop: Int?, topTime: Long?)

    @Query("update tmm_conversation set isMute = :isMute Where chatId = :chatId")
    fun updateMute(chatId: String?, isMute: Int?)

    @Query("update tmm_conversation set isMute = :isMute Where chatId in (:chatId)")
    fun updateMutes(chatId: MutableSet<String>, isMute: Int?)

    @Query("update tmm_conversation set name = :name Where chatId = :chatId")
    fun updateName(chatId: String, name: String?)

    @Query("update tmm_conversation set avatar = :avatar Where chatId = :chatId")
    fun updateGroupAvatar(chatId: String, avatar: String?)

    @Query("update tmm_conversation set introduce = :introduce, introduceIsRead = ${ConversationIntroduceConstant.CHAT_GROUP_INTRODUCE_DEFAULT} Where chatId = :chatId")
    fun updateGroupIntroduce(chatId: String, introduce: String?)

    @Query("update tmm_conversation set lastMessageIndex = :lastMessageIndex, hideSequence = :hideSequence Where chatId = :chatId")
    fun updateLastMessageIndex(chatId: String, lastMessageIndex: Long?, hideSequence: Long?)

    @Query("update tmm_conversation set lastMid = :lastMid Where chatId = :chatId")
    fun updateLastMessageMid(chatId: String, lastMid: String?)

    @Query("update tmm_conversation set isExistInGroup = :isExistInGroup Where chatId = (:chatIds)")
    fun updateExistInConversation(chatIds: MutableList<String>?, isExistInGroup: Int?)

    @Query("update tmm_conversation set lastMid = :lastMid Where chatId in (:chatIds)")
    fun updateLastMessageMids(chatIds: MutableList<String>?, lastMid: String?)

    @Query("update tmm_conversation set timeStamp = :timeStamp Where chatId = :chatId")
    fun updateConversationTimeStamp(chatId: String, timeStamp: Long?)

    @Query("update tmm_conversation set introduceIsRead = ${ConversationIntroduceConstant.CHAT_GROUP_INTRODUCE_READ} Where chatId = :chatId")
    fun updateGroupIntroduceIsRead(chatId: String)

    @Query("SELECT chatId from tmm_conversation group by chatId")
    fun queryChatIdsGroupByChatId(): MutableList<String>?

    @Query("select isMute from tmm_conversation Where chatId = :chatId")
    fun queryConversationMuteByChatId(chatId: String?): Int?

    @Query("delete from tmm_conversation where chatId in (:chatIds)")
    fun clearConversationByChatIds(chatIds: MutableSet<String>?)

    @Query("select chatId from tmm_conversation where chatId in (:chatIds)")
    fun queryExistConversationIds(chatIds: List<String>?): MutableList<String>?

    @Query("select chatId from tmm_conversation where chatId in (:chatIds) and name is null")
    fun queryNotCompleteConversationIds(chatIds: MutableList<String>?): MutableList<String>?

    @Query("select introduce from tmm_conversation Where chatId = :chatId")
    fun queryGroupIntroduce(chatId: String): String?


}