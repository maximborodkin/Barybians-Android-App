package ru.maxim.barybians.ui.activity.auth.login

import android.content.Context
import android.graphics.drawable.Animatable
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import kotlinx.coroutines.launch
import ru.maxim.barybians.R
import ru.maxim.barybians.databinding.FragmentLoginBinding
import ru.maxim.barybians.ui.activity.auth.login.LoginViewModel.LoginViewModelFactory
import ru.maxim.barybians.utils.appComponent
import ru.maxim.barybians.utils.clearDrawables
import ru.maxim.barybians.utils.setDrawableStart
import ru.maxim.barybians.utils.toast
import javax.inject.Inject

class LoginFragment : Fragment(R.layout.fragment_login) {

    @Inject
    lateinit var viewModelFactory: LoginViewModelFactory
    private val model: LoginViewModel by viewModels { viewModelFactory }

    private val binding by viewBinding(FragmentLoginBinding::bind)

    override fun onAttach(context: Context) {
        context.appComponent.inject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = model
        binding.loginBtn.setOnClickListener { model.login() }

        viewLifecycleOwner.lifecycleScope.launch {
            model.isLoginSuccess.observe(viewLifecycleOwner) { success ->
                if (success) {
                    findNavController().navigate(LoginFragmentDirections.loginToFeed())
                }
            }

            model.isLoading.observe(viewLifecycleOwner) { isLoading ->
                if (isLoading) {
                    binding.loginBtn.setDrawableStart(R.drawable.ic_timer_animated)
                    binding.loginBtn.compoundDrawablesRelative.firstOrNull()?.let {
                        it.setTint(binding.loginBtn.currentTextColor)
                        (it as? Animatable)?.start()
                    }
                } else {
                    binding.loginBtn.clearDrawables()
                }
            }
        }

        binding.loginRegisterLink.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.loginToRegistration())
        }
    }
}