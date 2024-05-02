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
export const useQueryParameters = qps => {
  useEffect(() => {
    const url = new URL(location.href);
    const params = url.searchParams;
    for (const qp of qps) {
      let v = params.get(qp.name);
      if (typeof qp.default === 'boolean') {
        qp.setter(v ? v === 'true' : qp.default);
      } else {
        if (v && Array.isArray(qp.default)) v = v.split(',');
        qp.setter(v || qp.default);
      }
    }
  }, []);

  const dependencies = qps.map(qp => qp.value);

  useEffect(() => {
    const url = new URL(location.href);
    let newUrl = url.origin + url.pathname;
    const params = {};

    // Add query parameters listed in qps that do not have their default value.
    for (const qp of qps) {
      let { toQP, value } = qp;
      if (toQP) value = toQP(value);
      if (value && !compare(value, qp.default)) params[qp.name] = value;
    }

    // Add query parameters that are not listed in qps.
    for (const [k, v] of url.searchParams) {
      if (!qps.some(qp => qp.name === k)) params[k] = v;
    }

    if (Object.keys(params).length) {
      newUrl += '?' + new URLSearchParams(params).toString();
    }
    history.replaceState(params, '', newUrl);
  }, dependencies);
};

const compare = (a, b) => stringValue(a) === stringValue(b);
const stringValue = v => (Array.isArray(v) ? v.sort().join(',') : v);