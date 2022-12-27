import React from "react";
import MyAnniversary from "./MyAnniversary";
import MyBirthday from "./MyBirthday";

import "./DoubleCelebration.css";

const DoubleCelebration = (props) => {
  const { hideMyBirthday, hideMyAnniversary, me, myAnniversary } = props;

  return (
    <div className="double-celebration">
      <div className="anniv">
        <MyAnniversary
          hideMyAnniversary={hideMyAnniversary}
          myAnniversary={myAnniversary}
        />
      </div>
      <div className="bday">
        <MyBirthday me={me} hideMyBirthday={hideMyBirthday} />
      </div>
    </div>
  );
};

export default DoubleCelebration;
