import "isomorphic-fetch";
import fetch from "jest-fetch-mock";
import requestAnimationFrame from "raf/polyfill";
import Enzyme, { shallow } from "enzyme";
import renderer from "react-test-renderer";
import Adapter from '@wojtekmaj/enzyme-adapter-react-17';
import "@testing-library/jest-dom/extend-expect";

Enzyme.configure({ adapter: new Adapter() });

global.snapshot = (component, options) =>
  expect(renderer.create(component, options).toJSON()).toMatchSnapshot();
global.shallowSnapshot = (component) =>
  expect(shallow(component)).toMatchSnapshot();

global.window = {};
global.fetch = fetch;
global.window.requestAnimationFrame = global.requestAnimationFrame = requestAnimationFrame;
