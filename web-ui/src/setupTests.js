import "isomorphic-fetch";
import React from "react";
import createFetchMock from 'vitest-fetch-mock';;
import requestAnimationFrame from "raf/polyfill";
import { render } from "@testing-library/react";
import {screen} from '@testing-library/dom';
import '@testing-library/jest-dom';

const mockModule = await vi.importActual("react-dom");

const mockClone = React.cloneElement;

vi.mock("react-dom", () => {
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
vi.mock("react-transition-group/cjs/Transition");

const fetchMocker = createFetchMock(vi);
fetchMocker.enableMocks();

global.snapshot = (component, options) =>
  expect(render(component, options).container).toMatchSnapshot();
global.rootSnapshot = (component, options) =>
  expect(render(component, options).baseElement).toMatchSnapshot();
global.waitForSnapshot = async (testId, component, options) => {
  const rendered = render(component, options);
  await screen.findByTestId(testId);
  expect(rendered.container).toMatchSnapshot();
}

global.window = global.window || {};
global.window.requestAnimationFrame = global.requestAnimationFrame = requestAnimationFrame;
//global.window.addEventListener = global.addEventListener;
