import React from "react";
import SkillCategoryNewDialog from "./SkillCategoryNewDialog";
import EnzymeToJson from 'enzyme-to-json';
import { mount } from 'enzyme';


it("renders correctly", () => {
  const dialog = mount(
    <SkillCategoryNewDialog
      isOpen={true}
      onClose={jest.fn()}
      onConfirm={jest.fn()}
    />
  );
  expect(EnzymeToJson(dialog)).toMatchSnapshot({});
});


