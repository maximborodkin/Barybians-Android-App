package ru.maxim.barybians.ui.fragment.registration

import android.Manifest
import android.app.DatePickerDialog
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.coroutines.launch
import ru.maxim.barybians.R
import ru.maxim.barybians.data.PreferencesManager
import ru.maxim.barybians.data.network.RetrofitClient
import ru.maxim.barybians.databinding.FragmentRegistrationBinding
import ru.maxim.barybians.ui.fragment.registration.RegistrationViewModel.RegistrationViewModelFactory
import ru.maxim.barybians.utils.*
import timber.log.Timber
import java.math.BigDecimal
import java.math.RoundingMode.UP
import java.util.*
import javax.inject.Inject

class RegistrationFragment : Fragment(R.layout.fragment_registration), DatePickerDialog.OnDateSetListener {

    @Inject
    lateinit var viewModelFactory: RegistrationViewModelFactory
    private val model: RegistrationViewModel by viewModels { viewModelFactory }

    @Inject
    lateinit var preferencesManager: PreferencesManager

    private val binding by viewBinding(FragmentRegistrationBinding::bind)
    private var getImageLauncher: ActivityResultLauncher<String>? = null

    override fun onAttach(context: Context) {
        context.appComponent.inject(this)
        getImageLauncher = registerForActivityResult(GetContent()) { uri: Uri? ->
            if (uri != null) {
                try {
                    val inputStream = context.contentResolver.openInputStream(uri) ?: throw IllegalStateException()
                    val bytes = inputStream.readBytes()
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    binding.registrationAvatar.setImageBitmap(bitmap)
                    binding.registrationAvatarClearButton.show()

                    binding.registrationAvatarDimensions.text =
                        getString(R.string.dimensions_placeholder, bitmap.width, bitmap.height)

                    val size = bytes.size.toDouble()
                    binding.registrationAvatarSize.text = when {
                        size >= 1_000_000 ->
                            getString(R.string.mbytes, BigDecimal(size / 1_000_000.0).setScale(2, UP).toString())
                        size >= 1_000 -> getString(R.string.kbytes, BigDecimal(size / 1_000.0).setScale(2, UP).toString())
                        else -> getString(R.string.bytes, size.toString())
                    }
                } catch (e: Exception) {
                    Timber.e(e)
                    if (preferencesManager.isDebug) context.longToast(e.message)
                    else context.toast(getString(R.string.unable_to_load_image))

                    resetImage()
                }
            }
        }
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?): Unit = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel = model
        registrationBackBtn.setOnClickListener { findNavController().popBackStack() }
        registrationBirthDate.keyListener = null
        registrationBirthDate
        registrationBirthDate.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                this@RegistrationFragment,
                model.birthDate.value?.get(Calendar.YEAR) ?: model.today.get(Calendar.YEAR),
                model.birthDate.value?.get(Calendar.MONTH) ?: model.today.get(Calendar.MONTH),
                model.birthDate.value?.get(Calendar.DAY_OF_MONTH) ?: model.today.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.datePicker.maxDate = Date().time
            datePickerDialog.show()
        }
        registrationMaleBtn.setOnClickListener { model.gender.postValue(false) }
        registrationFemaleBtn.setOnClickListener { model.gender.postValue(true) }

        registrationBtn.setOnClickListener { model.register() }
        registrationAvatar.setOnClickListener { pickImage() }
        registrationAvatarClearButton.setOnClickListener { resetImage() }

        viewLifecycleOwner.lifecycleScope.launch {
            model.errorMessageRes.observe(viewLifecycleOwner) { messageRes ->
                messageRes?.let { context.toast(it) }
            }

            model.isLoading.observe(viewLifecycleOwner) { isLoading ->
                if (isLoading) {
                    registrationBtn.setIconResource(R.drawable.ic_timer_animated)
                    (registrationBtn.icon as? Animatable)?.start()
                } else {
                    registrationBtn.icon = null
                }
            }

            model.isRegistrationSuccess.observe(viewLifecycleOwner) { success ->
                if (success) findNavController().navigate(RegistrationFragmentDirections.registrationToFeed())
            }
        }
        resetImage()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, dayOfMonth)
        }
        model.birthDate.postValue(calendar)
    }

    private fun pickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getImageLauncher?.launch("image/*")
        } else {
            Dexter
                .withContext(context)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                        getImageLauncher?.launch("image/*")
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse?) =
                        context.toast(getString(R.string.permission_required_for_get_image))

                    override fun onPermissionRationaleShouldBeShown(
                        request: PermissionRequest?,
                        token: PermissionToken?
                    ) {
                        token?.continuePermissionRequest()
                    }
                })
                .check()
        }
    }

    private fun resetImage() {
        Glide
            .with(context ?: return)
            .load(RetrofitClient.DEFAULT_AVATAR_URL)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.registrationAvatar.setImageResource(R.drawable.ic_camera)
                    return true
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ) = false
            })
            .into(binding.registrationAvatar)
        binding.registrationAvatarDimensions.text = getString(R.string.default_avatar)
        binding.registrationAvatarSize.text = null
        binding.registrationAvatarClearButton.hide()
    }
}