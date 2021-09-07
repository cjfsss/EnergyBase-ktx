package hos.util.extension

import android.text.TextUtils
import androidx.appcompat.app.AlertDialog
import hos.util.compat.ResContext
import hos.util.listener.OnCancelListener
import hos.util.listener.OnConfirmListener

/**
 * <p>Title: Dialog </p>
 * <p>Description:  </p>
 * <p>Company: www.mapuni.com </p>
 *
 * @author : 蔡俊峰
 * @date : 2020/9/1 9:51
 * @version : 1.0
 */


fun AlertDialog.Builder.asConfirmRes(
    titleId: Int?, contentId: Int? = 0, confirmId: Int? = 0, confirmListener: OnConfirmListener? = null,
    cancelId: Int? = 0, cancelListener: OnCancelListener? = null,
    isHideCancel: Boolean = false, bindLayoutId: Int = 0
): AlertDialog.Builder {
    var title: CharSequence? = null
    if (titleId != null && titleId != 0) {
        title = ResContext.getString(context, titleId)
    }
    var content: CharSequence? = null
    if (contentId != null && contentId != 0) {
        content = ResContext.getString(context, contentId)
    }
    var cancelBtnText: CharSequence? = null
    if (cancelId != null && cancelId != 0) {
        cancelBtnText = ResContext.getString(context, cancelId)
    }
    var confirmBtnText: CharSequence? = null
    if (confirmId != null && confirmId != 0) {
        confirmBtnText = ResContext.getString(context, confirmId)
    }
    return asConfirm(
        title, content, confirmBtnText, confirmListener,
        cancelBtnText, cancelListener, isHideCancel, bindLayoutId
    )
}

fun AlertDialog.Builder.asConfirm(
    title: CharSequence?, content: CharSequence? = null,
    confirmBtnText: CharSequence? = "确定", confirmListener: OnConfirmListener? = null,
    cancelBtnText: CharSequence? = "取消", cancelListener: OnCancelListener? = null,
    isHideCancel: Boolean = false, bindLayoutId: Int = 0
): AlertDialog.Builder {
    if (!TextUtils.isEmpty(title)) {
        setTitle(title)
    }
    if (!TextUtils.isEmpty(content)) {
        setMessage(content)
    }
    if (!isHideCancel && !TextUtils.isEmpty(cancelBtnText)) {
        setNegativeButton(cancelBtnText) { _, _ -> cancelListener?.onCancel() }
    }
    if (!TextUtils.isEmpty(confirmBtnText)) {
        setPositiveButton(confirmBtnText) { _, _ -> confirmListener?.onConfirm() }
    }
    if (bindLayoutId != 0) {
        setView(bindLayoutId)
    }
    return this
}

