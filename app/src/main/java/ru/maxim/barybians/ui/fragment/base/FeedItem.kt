package ru.maxim.barybians.ui.fragment.base

sealed class FeedItem {
    abstract fun getType(): FeedItemType
}

class HeaderItem(
    val userId: Int,
    val isPersonal: Boolean,
    val avatarSmall: String?,
    val avatarFull: String?,
    val name: String,
    val roleDrawable: Int,
    val roleDescription: Int,
    val birthDate: Long,
    val status: String?
) : FeedItem() {

    override fun getType() = FeedItemType.Header
}

class PostCreatorItem(
    val avatar: String?,
    var isExpanded: Boolean = false
) : FeedItem() {

    override fun getType() = FeedItemType.PostCreator
}

class PostItem(
    val postId: Int,
    val isPersonal: Boolean,
    val authorId: Int,
    val avatar: String?,
    val name: String,
    var date: String,
    var title: String?,
    var text: String,
    val likes: ArrayList<UserItem>,
    val comments: ArrayList<CommentItem>
) : FeedItem() {

    override fun getType() = FeedItemType.Post

    class UserItem(val id: Int, val name: String, val avatar: String?)
    class CommentItem(val id: Int, val text: String, val date: String, val author: UserItem)
}

enum class FeedItemType(val viewType: Int) {
    Header(0),
    PostCreator(1),
    Post(2)
}