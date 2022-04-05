package ru.maxim.barybians.ui.dialog.markupEditor

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager.widget.PagerAdapter
import by.kirich1409.viewbindingdelegate.viewBinding
import kotlinx.coroutines.launch
import ru.maxim.barybians.R
import ru.maxim.barybians.databinding.FragmentMarkupEditorBinding
import ru.maxim.barybians.databinding.ItemImageAttachmentBinding
import ru.maxim.barybians.domain.model.Attachment.AttachmentType.IMAGE
import ru.maxim.barybians.domain.model.Attachment.AttachmentType.STYLED
import ru.maxim.barybians.domain.model.Attachment.StyledAttachmentType.*
import ru.maxim.barybians.utils.appComponent
import ru.maxim.barybians.utils.load
import ru.maxim.barybians.utils.toast
import javax.inject.Inject

class MarkupEditorDialog : AppCompatDialogFragment(R.layout.fragment_markup_editor) {

    private val args: MarkupEditorDialogArgs by navArgs()
    private val binding by viewBinding(FragmentMarkupEditorBinding::bind)

    @Inject
    lateinit var factory: MarkupEditorViewModel.MarkupEditorViewModelFactory.Factory
    private val model: MarkupEditorViewModel by viewModels {
        factory.create(
            text = args.text ?: String(),
            attachments = args.attachments?.toList() ?: emptyList()
        )
    }

    override fun onAttach(context: Context) {
        context.appComponent.inject(this)
        super.onAttach(context)
    }

    override fun onStart() {
        super.onStart()
        setStyle(DialogFragment.STYLE_NO_TITLE, 0)
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        val backgroundColor = ContextCompat.getColor(context ?: return, R.color.colorBackground)
        // Setting a background removes the margins from the dialog
        dialog?.window?.setBackgroundDrawable(ColorDrawable(backgroundColor))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?): Unit = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = model

        markupEditorToolbar.title = getString(args.title)
        markupEditorPager.adapter = MarkupEditorPagerAdapter()
        markupEditorToolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        markupEditorTabLayout.setupWithViewPager(binding.markupEditorPager)
        markupEditorTabLayout.getTabAt(0)?.setIcon(R.drawable.ic_code)
        markupEditorTabLayout.getTabAt(1)?.setIcon(R.drawable.ic_image)

        markupImageAttachmentButton.setOnClickListener {
            // TODO: add image attachment
        }

        viewLifecycleOwner.lifecycleScope.launch {
            model.attachments.observe(viewLifecycleOwner) { applyAttachments() }
        }

        applyTextStyle()
        applyAttachments()
    }

    private fun applyTextStyle() = with(binding) {
        val spannableString = SpannableStringBuilder(model.text.value)

        model.attachments.value?.filter { it.type == STYLED && it.style != null }?.forEach { attachment ->
            if (attachment.style != null) {
                when (attachment.style) {
                    BOLD -> {
                        spannableString.setSpan(
                            StyleSpan(Typeface.BOLD),
                            attachment.offset,
                            attachment.offset + attachment.length,
                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                        )
                    }
                    ITALIC ->
                        spannableString.setSpan(
                            StyleSpan(Typeface.ITALIC),
                            attachment.offset,
                            attachment.offset + attachment.length,
                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                        )
                    UNDERLINE -> {
                        spannableString.setSpan(
                            UnderlineSpan(),
                            attachment.offset,
                            attachment.offset + attachment.length,
                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                        )
                    }
                    STRIKE -> {
                        spannableString.setSpan(
                            StrikethroughSpan(),
                            attachment.offset,
                            attachment.offset + attachment.length,
                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                        )
                    }
                }
            }
        }

        markupEditorPreviewPage.setText(spannableString, TextView.BufferType.SPANNABLE)
    }

    private fun applyAttachments() = with(binding) {
        markupEditorAttachmentsHolder.removeAllViews()
        context.toast(model.attachments.value?.size.toString())
        model.attachments.value?.filter { it.type == IMAGE && it.url != null }?.forEach { attachment ->
            ItemImageAttachmentBinding.inflate(layoutInflater, markupEditorAttachmentsHolder, false).apply {
                imageAttachmentImage.load(attachment.url)
                imageAttachmentImage.setOnClickListener {
                    val action = MarkupEditorDialogDirections.toImageViewer(attachment.url ?: return@setOnClickListener)
                    findNavController().navigate(action)
                }
                imageAttachmentDeleteButton.setOnClickListener { model.removeAttachment(attachment) }
                markupEditorAttachmentsHolder.addView(this.root)
            }
        }
    }

    private inner class MarkupEditorPagerAdapter : PagerAdapter() {

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            return when (position) {
                0 -> binding.markupEditorEditorPage
                1 -> {
                    applyTextStyle()
                    binding.markupEditorPreviewPage
                }
                else -> throw IllegalArgumentException("No view for position $position")
            }
        }

        override fun getCount(): Int = 2

        override fun isViewFromObject(view: View, obj: Any): Boolean = view == obj
    }
}