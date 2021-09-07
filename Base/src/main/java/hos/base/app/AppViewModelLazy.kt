package hos.base.app

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelProvider
import hos.core.AppCompatApplication

/**
 * <p>Title: DelegateViewModelLazy </p>
 * <p>Description:  </p>
 * <p>Company: www.mapuni.com </p>
 *
 * @author : 蔡俊峰
 * @date : 2020/12/15 16:05
 * @version : 1.0
 */


@MainThread
inline fun <reified VM : ViewModel> AppCompatApplication.viewModels(
    noinline factoryProducer: (() -> ViewModelProvider.Factory)? = null
): Lazy<VM> {
    val factoryPromise = factoryProducer ?: {
        defaultViewModelProviderFactory
    }
    return ViewModelLazy(VM::class, { viewModelStore }, factoryPromise)
}

