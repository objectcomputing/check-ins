import React from "react";
import ReactDOM from "react-dom";
import {AppContextProvider} from "../../context/AppContext";
import SkillCategoryNewDialog from "./SkillCategoryNewDialog";

describe("SkillCategoryNewDialog", () => {
  it("renders correctly", () => {
    snapshot(
      <AppContextProvider>
        <SkillCategoryNewDialog
          isOpen={true}
          onClose={vi.fn()}
          onConfirm={vi.fn()}
        />
      </AppContextProvider>
    );
  });
});


