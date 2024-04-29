import { useEffect } from 'react';

/**
 * @typedef {object} QPBoolean
 * @property {string} name
 * @property {boolean} default
 * @property {boolean} value
 * @property {(boolean) => void} setter - takes query parameter value and updates state
 * @property {[(any) => string]} toQP - takes state value and returns query parameter value
 */

/**
 * @typedef {object} QPString
 * @property {string} name
 * @property {string} default
 * @property {string} value
 * @property {(string) => void} setter - takes query parameter value and updates state
 * @property {[(any) => string]} toQP - takes state value and returns query parameter value
 */

/**
 * @param {(QPBoolean | QPString)[]} qps - query parameters
 */
export const queryParameterSetup = qps => {
  useEffect(() => {
    const url = new URL(location.href);
    const params = url.searchParams;
    for (const qp of qps) {
      const v = params.get(qp.name);
      if (typeof qp.default === 'boolean') {
        qp.setter(v ? v === 'true' : qp.default);
      } else {
        qp.setter(v || qp.default);
      }
    }
  }, []);

  const dependencies = qps.map(qp => qp.value);

  // This assumes the app does not use query parameters for any other purposes.
  // It will drop any that are not set by this useEffect.
  useEffect(() => {
    const url = new URL(location.href);
    let newUrl = url.origin + url.pathname;
    const params = {};
    for (const qp of qps) {
      let { toQP, value } = qp;
      if (toQP) value = toQP(value);
      if (value && value !== qp.default) params[qp.name] = value;
    }
    if (Object.keys(params).length) {
      newUrl += '?' + new URLSearchParams(params).toString();
    }
    history.replaceState(params, '', newUrl);
  }, dependencies);
};
