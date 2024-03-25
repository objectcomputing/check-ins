import React from "react";
import SelectSkillsDialog from "./SelectSkillsDialog";
import EnzymeToJson from 'enzyme-to-json';
import { mount } from 'enzyme';

const skill = {
  id: "skill-id",
  name: "Java",
  description: "A programming language",
  pending: false,
  extraneous: true
};

it("renders correctly", () => {
  const dialog = mount(
    <SelectSkillsDialog
      isOpen={true}
      onClose={jest.fn()}
      selectableSkills={[skill]}
      onSave={jest.fn()}
    />
  );
  expect(EnzymeToJson(dialog)).toMatchSnapshot({});
});



