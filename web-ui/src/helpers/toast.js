import { UPDATE_TOAST } from '../context/actions';

export const showToast = (severity, toast) => {
  if (window.snackDispatch) {
    window.snackDispatch({
      type: UPDATE_TOAST,
      payload: {
        severity,
        toast,
      }
    });
  }
};

export const showError = msg => showToast('error', msg);

export const showInfo = msg => showToast('info', msg);

export const showSuccess = msg => showToast('success', msg);

export const showWarning = msg => showToast('warning', msg);
