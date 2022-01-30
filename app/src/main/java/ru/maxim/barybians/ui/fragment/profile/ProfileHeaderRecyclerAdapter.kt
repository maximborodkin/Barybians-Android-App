package ru.maxim.barybians.ui.fragment.profile

//import ru.maxim.barybians.ui.fragment.base.FeedItem

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.maxim.barybians.databinding.ItemProfileHeaderBinding
import ru.maxim.barybians.domain.model.User
import ru.maxim.barybians.ui.fragment.profile.ProfileHeaderRecyclerAdapter.ProfileHeaderViewHolder
import javax.inject.Inject

class ProfileHeaderRecyclerAdapter @Inject constructor() :
    ListAdapter<User, ProfileHeaderViewHolder>(ProfileHeaderDiffUtil) {

    private var profileItemsListener: ProfileItemsListener? = null

    fun setProfileItemsListener(listener: ProfileItemsListener?) {
        profileItemsListener = listener
    }

    inner class ProfileHeaderViewHolder(
        private val binding: ItemProfileHeaderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) = with(binding) {
            binding.user = user
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileHeaderViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemProfileHeaderBinding.inflate(layoutInflater, parent, false)
        return ProfileHeaderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProfileHeaderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private object ProfileHeaderDiffUtil : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean = true

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean = oldItem == newItem
    }
}

//        val okBtn: MaterialButton = view.itemPostCreatorOkBtn
//        fun expand() {
//            labelView.visibility = View.GONE
//            titleLayoutView.visibility = View.VISIBLE
//            textLayoutView.visibility = View.VISIBLE
//            buttonsLayout.visibility = View.VISIBLE
//            view.isClickable = false
//        }
//
//        fun reduce() {
//            labelView.visibility = View.VISIBLE
//            titleLayoutView.visibility = View.GONE
//            textLayoutView.visibility = View.GONE
//            buttonsLayout.visibility = View.GONE
//            view.isClickable = true
//        }
//
//        fun reset() {
//            reduce()
//            titleView.clearFocus()
//            titleView.text = null
//            text.clearFocus()
//            text.text = null
//            val inputMethodManager =
//                itemView.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
//            inputMethodManager.hideSoftInputFromWindow(itemView.windowToken, 0)
//        }
//    }
//
//    override fun bindHeaderViewHolder(
//        headerViewHolder: HeaderViewHolder,
//        position: Int,
//        context: Context
//    ) {
//        super.bindHeaderViewHolder(headerViewHolder, position, context)
//        if (headerViewHolder.isBinded) return
//        val header = feedItems[position] as HeaderItem
//
//        if (header.isPersonal) {
//            headerViewHolder.backBtn.visibility = GONE
//            headerViewHolder.openChatBtn.visibility = GONE
//            headerViewHolder.preferencesBtn.visibility = VISIBLE
//            headerViewHolder.preferencesBtn.setOnClickListener { profileItemsListener.openPreferences() }
//            headerViewHolder.editBtn.visibility = VISIBLE
//            headerViewHolder.editBtn.setOnClickListener { profileItemsListener.editUserInfo() }
//        } else {
//            headerViewHolder.backBtn.apply {
//                visibility = VISIBLE
//                setOnClickListener { /*profileItemsListener.popBackStack()*/ }
//            }
//            headerViewHolder.openChatBtn.apply {
//                visibility = VISIBLE
//                setOnClickListener {
//                    profileItemsListener.openDialog(
//                        header.userId,
//                        header.avatarSmall,
//                        header.name
//                    )
//                }
//            }
//            headerViewHolder.preferencesBtn.visibility = GONE
//            headerViewHolder.editBtn.visibility = GONE
//        }
//
//        Glide.with(context).load(header.avatarSmall).into(headerViewHolder.avatarView)
//        headerViewHolder.avatarView.setOnClickListener {
//            if (!header.avatarFull.isNullOrBlank())
//                onImageClick(header.avatarFull)
//        }
//        Glide.with(context).load(header.avatarSmall).into(headerViewHolder.backgroundView)
//
//        headerViewHolder.nameView.apply {
//            text = header.name
//            setCompoundDrawablesWithIntrinsicBounds(0, 0, header.roleDrawable, 0)
//            setOnTouchListener(View.OnTouchListener { view, event ->
//                view.performClick()
//                return@OnTouchListener if (event.action == MotionEvent.ACTION_DOWN &&
//                    event.rawX >= right - compoundDrawables[2].bounds.width()
//                ) {
//                    context.toast(header.roleDescription)
//                    true
//                } else false
//            })
//        }
//
//        val age =
//            ((Date().time / 1000 - Date(header.birthDate).time) / (60 * 60 * 24 * 365)).toInt()
//        headerViewHolder.ageView.text =
//            context.resources.getQuantityString(R.plurals.age_plurals, age, age)
//
//        if (!header.isPersonal && header.status.isNullOrBlank()) {
//            headerViewHolder.statusView.visibility = GONE
//        } else if (header.isPersonal && header.status.isNullOrBlank()) {
//            headerViewHolder.statusView.text = context.getString(R.string.enter_your_status)
//            headerViewHolder.statusView.setDrawableEnd(R.drawable.ic_edit_grey)
//        } else {
//            headerViewHolder.statusView.text = header.status
//            headerViewHolder.statusView.clearDrawables()
//        }
//
//        if (header.isPersonal) {
//            headerViewHolder.statusView.setOnClickListener {
//                profileItemsListener.showEditStatusDialog(header.status)
//            }
//        }
//
//        headerViewHolder.isBinded = true
//    }
//
//    override fun bindPostCreatorViewHolder(
//        postCreatorViewHolder: PostCreatorViewHolder,
//        position: Int,
//        context: Context
//    ) {
//        super.bindPostCreatorViewHolder(postCreatorViewHolder, position, context)
//        val postCreator = feedItems[position] as PostCreatorItem
//
//        Glide.with(context).load(postCreator.avatar).into(postCreatorViewHolder.avatarView)
//
//        postCreatorViewHolder.cameraBtn.setOnClickListener {
//            // TODO("Make photo upload")
//            Toast.makeText(context, "Photo upload here", Toast.LENGTH_SHORT).show()
//        }
//
//        if (postCreator.isExpanded) postCreatorViewHolder.expand()
//        else postCreatorViewHolder.reduce()
//
//        postCreatorViewHolder.itemView.setOnClickListener {
//            if (!postCreator.isExpanded) {
//                postCreator.isExpanded = true
//                postCreatorViewHolder.expand()
//            }
//        }
//        postCreatorViewHolder.cancelBtn.setOnClickListener {
//            if (postCreator.isExpanded) {
//                postCreator.isExpanded = false
//                postCreatorViewHolder.reduce()
//            }
//        }
//
//        postCreatorViewHolder.text.addTextChangedListener {
//            if (it.toString().isNotBlank()) {
//                postCreatorViewHolder.textLayoutView.error = null
//            }
//        }
//
//        postCreatorViewHolder.okBtn.setOnClickListener {
//            val title = postCreatorViewHolder.titleView.text.toString()
//            val text = postCreatorViewHolder.text.text.toString()
//            if (text.isBlank()) {
//                postCreatorViewHolder.textLayoutView.error =
//                    context.getString(R.string.this_field_is_required)
//            } else {
//                postCreatorViewHolder.textLayoutView.error = null
//                profileItemsListener.addPost(title, text)
//            }
//        }
//    }
//}