package ru.maxim.barybians.ui.fragment.registration

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
import ru.maxim.barybians.ui.fragment.registration.RegistrationViewModel.RegistrationViewModelFactory
import ru.maxim.barybians.utils.*
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