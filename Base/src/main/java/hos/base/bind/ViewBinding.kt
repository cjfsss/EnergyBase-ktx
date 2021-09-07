@file:Suppress("unused")

package hos.base.bind

import android.app.Activity
import android.app.Dialog
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.viewbinding.ViewBinding
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun <V : View> Activity.find(id: Int) = lazy {
    return@lazy findViewById<V>(id) ?: null
}

fun <V : View> find(view: View?, id: Int) = lazy {
    return@lazy view?.findViewById<V>(id)
}

fun <V : View> Fragment.find(id: Int) = lazy {
    return@lazy view?.findViewById<V>(id)
}

fun <V : View> Dialog.find(id: Int) = lazy {
    return@lazy findViewById<V>(id) ?: null
}

fun <V : View> ViewGroup.find(id: Int) = lazy {
    return@lazy findViewById<V>(id) ?: null
}

@RequiresApi(Build.VERSION_CODES.P)
fun <V : View> Activity.requireView(id: Int) = lazy {
    return@lazy requireViewById<V>(id)
}

@RequiresApi(Build.VERSION_CODES.P)
fun <V : View> requireView(view: View?, id: Int) = lazy {
    return@lazy view?.requireViewById<V>(id)
}

@RequiresApi(Build.VERSION_CODES.P)
fun <V : View> Fragment.requireView(id: Int) = lazy {
    return@lazy view?.requireViewById<V>(id)
}

@RequiresApi(Build.VERSION_CODES.P)
fun <V : View> Dialog.requireView(id: Int) = lazy {
    return@lazy requireViewById<V>(id)
}

@RequiresApi(Build.VERSION_CODES.P)
fun <V : View> ViewGroup.requireView(id: Int) = lazy {
    return@lazy requireViewById<V>(id)
}

fun <VB : ViewBinding> ComponentActivity.bindingRoot(inflate: (LayoutInflater) -> VB) = lazy {
    inflate(layoutInflater).also {
        setContentView(it.root)
        if (this is ViewDataBinding) lifecycleOwner = this@bindingRoot
    }
}

fun <VB : ViewBinding> ComponentActivity.binding(inflate: (LayoutInflater) -> VB) = lazy {
    inflate(layoutInflater).also {
        if (this is ViewDataBinding) lifecycleOwner = this@binding
    }
}


fun <VB : ViewBinding> Fragment.binding(bind: (View) -> VB) = FragmentBindingDelegate(bind)


fun <VB : ViewBinding> Dialog.bindingRoot(inflate: (LayoutInflater) -> VB) = lazy {
    inflate(layoutInflater).also { setContentView(it.root) }
}

fun <VB : ViewBinding> Dialog.binding(inflate: (LayoutInflater) -> VB) = lazy {
    inflate(layoutInflater)
}

fun <VB : ViewBinding> ViewGroup.binding(
    inflate: (LayoutInflater, ViewGroup?, Boolean) -> VB,
    attachToParent: Boolean = true
) = lazy {
    inflate(LayoutInflater.from(context), if (attachToParent) this else null, attachToParent)
}

fun <VB : ViewBinding> TabLayout.Tab.setCustomView(
    inflate: (LayoutInflater) -> VB,
    onBindView: VB.() -> Unit
) {
    customView = inflate(LayoutInflater.from(parent!!.context)).apply(onBindView).root
}

inline fun <reified VB : ViewBinding> TabLayout.Tab.bindCustomView(
    bind: (View) -> VB,
    onBindView: VB.() -> Unit
) =
    customView?.let { bind(it) }?.run(onBindView)

inline fun <reified VB : ViewBinding> NavigationView.setHeaderView(
    index: Int = 0,
    bind: (View) -> VB,
    onBindView: VB.() -> Unit
) =
    getHeaderView(index)?.let { bind(it) }?.run(onBindView)

inline fun Fragment.doOnDestroyView(crossinline block: () -> Unit) =
    viewLifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroyView() {
            block.invoke()
        }
    })

interface BindingLifecycleOwner {
    fun onDestroyViewBinding(destroyingBinding: ViewBinding)
}

class FragmentBindingDelegate<VB : ViewBinding>(private val bind: (View) -> VB) :
    ReadOnlyProperty<Fragment, VB> {
    private var binding: VB? = null

    override fun getValue(thisRef: Fragment, property: KProperty<*>): VB {
        if (binding == null) {
            binding = bind(thisRef.requireView()).also {
                if (it is ViewDataBinding) it.lifecycleOwner = thisRef.viewLifecycleOwner
            }
            thisRef.doOnDestroyView {
                if (thisRef is BindingLifecycleOwner) thisRef.onDestroyViewBinding(binding!!)
                binding = null
            }
        }
        return binding!!
    }
}