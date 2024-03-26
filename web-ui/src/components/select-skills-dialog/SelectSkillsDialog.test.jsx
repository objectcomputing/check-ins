import React from "react";
import ReactDOM from "react-dom";
import {AppContextProvider} from "../../context/AppContext";
import SelectSkillsDialog from "./SelectSkillsDialog";

const skill = {
  id: "skill-id",
  name: "Java",
  description: "A programming language",
  pending: false,
  extraneous: true
};

describe("SelectSkillsDialog", () => {
  it("renders correctly", () => {
    snapshot(
    <AppContextProvider>
      <SelectSkillsDialog
        isOpen={true}
        onClose={jest.fn()}
        selectableSkills={[skill]}
        onSave={jest.fn()}
      />
    </AppContextProvider>
    );
  });
});
