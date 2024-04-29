import { useEffect } from 'react';

/**
 * @typedef {object} QPBoolean
 * @property {string} name
 * @property {boolean} default
 * @property {() => boolean} getter
 * @property {(boolean) => void} setter
 */

/**
 * @typedef {object} QPString
 * @property {string} name
 * @property {string} default
 * @property {() => string} getter
 * @property {(string) => void} setter
 */

/**
 * @param {(QPBoolean | QPString[]} qps - query parameters
 */
export const queryParameterSetup = qps => {
  useEffect(() => {
    const url = new URL(location.href);

    const params = url.searchParams;

    for (const qp of qps) {
      const v = params.get(qp.name);
      if (typeof qp.default === 'boolean') {
        qp.setter(v === 'true');
      } else {
        qp.setter(v || qp.default);
      }
    }
  }, []);

  const dependencies = qps.map(qp => qp.getter());

  useEffect(() => {
    const url = new URL(location.href);
    let newUrl = url.origin + url.pathname;
    const params = {};
    for (const qp of qps) {
      const value = qp.getter();
      if (value && value !== qp.default) params[qp.name] = value;
    }
    if (Object.keys(params).length) {
      newUrl += '?' + new URLSearchParams(params).toString();
    }
    history.replaceState(params, '', newUrl);
  }, dependencies);
};
