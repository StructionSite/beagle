package com.pandulapeter.beagle.appDemo

import com.pandulapeter.beagle.appDemo.feature.main.about.AboutViewModel
import com.pandulapeter.beagle.appDemo.feature.main.about.licences.LicencesViewModel
import com.pandulapeter.beagle.appDemo.feature.main.inspiration.InspirationViewModel
import com.pandulapeter.beagle.appDemo.feature.main.inspiration.authentication.AuthenticationViewModel
import com.pandulapeter.beagle.appDemo.feature.main.inspiration.basicSetup.BasicSetupViewModel
import com.pandulapeter.beagle.appDemo.feature.main.inspiration.featureToggles.FeatureTogglesViewModel
import com.pandulapeter.beagle.appDemo.feature.main.playground.PlaygroundViewModel
import com.pandulapeter.beagle.appDemo.feature.main.setup.SetupViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

private val featureModule = module {
    viewModel { SetupViewModel() }
    viewModel { InspirationViewModel() }
    viewModel { BasicSetupViewModel() }
    viewModel { AuthenticationViewModel() }
    viewModel { FeatureTogglesViewModel() }
    viewModel { PlaygroundViewModel() }
    viewModel { AboutViewModel() }
    viewModel { LicencesViewModel() }
}

val modules = featureModule