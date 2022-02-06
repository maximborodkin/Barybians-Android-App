package ru.maxim.barybians.ui.activity.auth.registration

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.drawable.Animatable
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import kotlinx.coroutines.launch
import ru.maxim.barybians.R
import ru.maxim.barybians.databinding.FragmentRegistrationBinding
import ru.maxim.barybians.ui.activity.auth.registration.RegistrationViewModel.RegistrationViewModelFactory
import ru.maxim.barybians.utils.appComponent
import ru.maxim.barybians.utils.setDrawableStart
import ru.maxim.barybians.utils.toast
import java.util.*
import javax.inject.Inject

class RegistrationFragment : Fragment(R.layout.fragment_registration), DatePickerDialog.OnDateSetListener {

    @Inject
    lateinit var viewModelFactory: RegistrationViewModelFactory
    private val model: RegistrationViewModel by viewModels { viewModelFactory }

    private val binding by viewBinding(FragmentRegistrationBinding::bind)

    override fun onAttach(context: Context) {
        context.appComponent.inject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        with(binding) {
            viewModel = model
            registrationBackBtn.setOnClickListener { findNavController().popBackStack() }
            registrationBirthDate.keyListener = null
            registrationBirthDate.setOnClickListener {
                DatePickerDialog(
                    requireContext(),
                    this@RegistrationFragment,
                    model.today.get(Calendar.YEAR),
                    model.today.get(Calendar.MONTH),
                    model.today.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
            registrationMaleBtn.setOnClickListener { model.sex.postValue(false) }
            registrationFemaleBtn.setOnClickListener { model.sex.postValue(true) }

            registrationBtn.setOnClickListener {
                binding.registrationBtn.apply {
                    setDrawableStart(R.drawable.ic_timer_animated)
                    compoundDrawablesRelative.firstOrNull()?.let {
                        it.setTint(currentTextColor)
                        (it as? Animatable)?.start()
                    }
                }
                model.register()
            }

            viewLifecycleOwner.lifecycleScope.launch {
                model.errorMessageRes.observe(viewLifecycleOwner) { messageRes ->
                    if (messageRes == R.string.login_already_exists) {
                        binding.registrationLoginLayout.error = getString(messageRes)
                    } else {
                        messageRes?.let { context?.toast(it) }
                    }
                }

                model.isRegistrationSuccess.observe(viewLifecycleOwner) { success ->
                    if (success) {
//
                    }
                }
            }
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, dayOfMonth)
        }
        model.birthDate.postValue(calendar)
    }
}