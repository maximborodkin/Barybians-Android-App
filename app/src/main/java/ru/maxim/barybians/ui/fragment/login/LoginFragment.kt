package ru.maxim.barybians.ui.fragment.login

import android.content.Context
import android.content.Intent
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
import ru.maxim.barybians.data.PreferencesManager
import ru.maxim.barybians.databinding.FragmentLoginBinding
import ru.maxim.barybians.service.WebSocketService
import ru.maxim.barybians.ui.fragment.login.LoginViewModel.LoginViewModelFactory
import ru.maxim.barybians.utils.*
import javax.inject.Inject

class LoginFragment : Fragment(R.layout.fragment_login) {

    @Inject
    lateinit var viewModelFactory: LoginViewModelFactory
    private val model: LoginViewModel by viewModels { viewModelFactory }

    @Inject
    lateinit var preferencesManager: PreferencesManager

    private val binding by viewBinding(FragmentLoginBinding::bind)

    override fun onAttach(context: Context) {
        context.appComponent.inject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = model
        loginBtn.setOnClickListener { model.login() }

        viewLifecycleOwner.lifecycleScope.launch {
            model.isLoginSuccess.observe(viewLifecycleOwner) { success ->
                if (success) {
                    findNavController().navigate(LoginFragmentDirections.loginToFeed())
                    val messageServiceIntent = Intent(context, WebSocketService::class.java)
                    activity?.startService(messageServiceIntent)
                }
            }

            model.isLoading.observe(viewLifecycleOwner) { isLoading ->
                if (isLoading) {
                    loginBtn.setIconResource(R.drawable.ic_timer_animated)
                    (loginBtn.icon as? Animatable)?.start()
                } else {
                    loginBtn.icon = null
                }
            }
        }

        loginRegisterLink.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.loginToRegistration())
        }

        loginDarkModeButton.setOnClickListener {
            val isDarkMode = model.isDarkMode.value?.not() ?: false
            model.isDarkMode.value = isDarkMode
            preferencesManager.isDarkMode = isDarkMode
            activity?.recreate()
        }
    }

    override fun onResume() {
        super.onResume()
        model.isDarkMode.value = preferencesManager.isDarkMode
    }
}