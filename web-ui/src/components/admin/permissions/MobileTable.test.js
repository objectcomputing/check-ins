import React from "react";
import renderer from 'react-test-renderer';
import MobileTable from "./MobileTable"
import {roles, allPermissions, handleChange} from "./sample-data"

it("renders correctly", () => {
  const component = renderer.create(
    <MobileTable 
      roles={roles} 
      allPermissions={allPermissions}
      handleChange={handleChange}
    />
  )
  expect(component.toJSON()).toMatchSnapshot();
});