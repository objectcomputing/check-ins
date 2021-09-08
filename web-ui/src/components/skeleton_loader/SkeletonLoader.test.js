import React from "react";
import renderer from 'react-test-renderer';
import SkeletonLoader from "./SkeletonLoader"




it("renders correctly when 'team' is passed as prop ", () => {
  const component = renderer.create(
    <SkeletonLoader type="team"/>
  )
  expect(component.toJSON()).toMatchSnapshot();
});

it("renders correctly when 'guild' is passed as prop ", () => {
  const component = renderer.create(
    <SkeletonLoader type="guild"/>
  )
  expect(component.toJSON()).toMatchSnapshot();
});

it("renders correctly when 'people' is passed as prop ", () => {
    const component = renderer.create(
      <SkeletonLoader type="people"/>
    )
    expect(component.toJSON()).toMatchSnapshot();
});