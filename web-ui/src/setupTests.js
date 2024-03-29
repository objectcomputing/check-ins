import "isomorphic-fetch";
import React from "react";
import fetch from "jest-fetch-mock";
import requestAnimationFrame from "raf/polyfill";
import renderer from "react-test-renderer";
import "@testing-library/jest-dom";
import {jest} from '@jest/globals';

const mockModule = jest.requireActual("react-dom");

const mockClone = React.cloneElement;

jest.mock("react-dom", () => {
  return {
    __esModule: true, // Use it when dealing with esModules
    ...mockModule,
    createPortal: (element, target) => {
      if (!element.style) {
        return mockClone(element, { style: { webkitTransition: '' } });
      }
      return element;
    },
  };
});
jest.mock("react-transition-group/cjs/Transition");

global.snapshot = (component, options) =>
  expect(renderer.create(component, options).toJSON()).toMatchSnapshot();

global.window = {};
global.fetch = fetch;
global.window.requestAnimationFrame = global.requestAnimationFrame = requestAnimationFrame;
