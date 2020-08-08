package ru.maxim.barybians.ui.fragment.profile

sealed class ProfileItem {
    abstract fun getType(): ProfileItemType
}

class ProfileItemHeader(val isPersonal: Boolean, val avatar: String?, val name: String, val roleDrawable: Int,
                        val roleDescription: Int, val birthDate: Long, val status: String?) : ProfileItem() {
    override fun getType() = ProfileItemType.Header
}

class ProfileItemPostCreator(val avatar: String?, var isExpanded: Boolean = false) : ProfileItem() {
    override fun getType() = ProfileItemType.PostCreator
}

class ProfileItemPost(val postId: Int, val isPersonal: Boolean, val avatar: String?,
                      val name: String, var date: String, var title: String?, var text: String,
                      val likes: ArrayList<ItemUser>, val comments: ArrayList<ItemComment>) : ProfileItem() {
    override fun getType() = ProfileItemType.Post

    class ItemUser(val id: Int, val name: String, val avatar: String?)
    class ItemComment(val id: Int, val text: String, val date: String, val author: ItemUser)
}

enum class ProfileItemType(val viewType: Int) {
    Header(0),
    PostCreator(1),
    Post(2)
}