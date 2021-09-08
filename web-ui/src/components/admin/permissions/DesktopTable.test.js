import React from "react";
import renderer from 'react-test-renderer';
import DesktopTable from "./DesktopTable"
import {roles, allPermissions, handleChange} from "./sample-data"

it("renders correctly", () => {
  const component = renderer.create(
    <DesktopTable 
      roles={roles} 
      allPermissions={allPermissions}
      handleChange={handleChange}
    />
  )
  expect(component.toJSON()).toMatchSnapshot();
});