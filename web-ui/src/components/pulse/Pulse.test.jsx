import React from 'react';
import renderer from 'react-test-renderer';
import Pulse from './Pulse';

it("renders correctly when 'kudosFrom' prop is provided", () => {
  const component = renderer.create(
    <Pulse
      comment="Just testing"
      score={2}
      setComment={() => {}}
      setScore={() => {}}
    />
  );
  expect(component.toJSON()).toMatchSnapshot();
});
