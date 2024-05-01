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
export const useQueryParameters = (
  qps,
  requirements = [],
  processedQPs = null
) => {
  // This examines all the query parameters and
  // sets the state value associated with each of them.
  useEffect(() => {
    if (processedQPs?.current) return;

    //console.log('useQueryParameters: requirements =', requirements);
    const haveRequirements = requirements.every(req =>
      Array.isArray(req) ? req.length > 0 : req !== null && req !== undefined
    );
    if (!haveRequirements) return;

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
    if (processedQPs) processedQPs.current = true;
  }, requirements);

  const dependencies = qps.map(qp => qp.value);

  // This updates the query parameters in the URL
  // when their associated state values change.
  useEffect(() => {
    const haveRequirements = requirements.every(req =>
      Array.isArray(req) ? req.length > 0 : req !== null && req !== undefined
    );
    if (!haveRequirements) return;

    const url = new URL(location.href);
    const baseUrl = url.origin + url.pathname;
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

    const search = Object.keys(params).length
      ? '?' + new URLSearchParams(params).toString()
      : '';
    if (search !== url.search) {
      history.replaceState(params, '', baseUrl + search);
    }
  }, dependencies);
};

const compare = (a, b) => stringValue(a) === stringValue(b);
const stringValue = v => (Array.isArray(v) ? v.sort().join(',') : v);
