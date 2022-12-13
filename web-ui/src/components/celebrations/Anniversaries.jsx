import React, { useState } from "react";

import "./Anniversaries.css";

const Anniversaries = () => {
  const [showAnniversaries, setShowAnniversaries] = useState(false);
  const text = "TESTING";

  return (
    <div
      className={showAnniversaries ? "gift dukdik" : "gift"}
      onClick={() => setShowAnniversaries(!showAnniversaries)}
    >
      <div className={showAnniversaries ? "gift-top open" : "gift-top"}></div>
      <h1 className={showAnniversaries ? "gift-text open" : "gift-text"}>
        {text}
      </h1>
      <div
        className={showAnniversaries ? "gift-box boxDown" : "gift-box"}
      ></div>
    </div>
    // <div id="app">
    // <div class="gift" :class="{dukdik: !open}" @click="click">
    //     <div class="gift-top"
    //         :class="{boxOpen: open}"
    //     ></div>
    //     <h1 class="gift-text" v-if="open">{{text}}</h1>
    //     <div class="gift-box"
    //         :class="{boxDown: open}"
    //     ></div>
    // </div>
    // </div>
  );
};

export default Anniversaries;

// var app = new Vue({
//   el: "#app",
//   data: {
//     open: false,
//     text: "Thank you for 5 Years!",
//   },
//   methods: {
//     click: function () {
//       this.open = !this.open;
//     },
//   },
//   watch: {
//     open: function () {
//       if (this.open == true) {
//         document.body.className = "open";
//       } else {
//         document.body.className = "";
//       }
//     },
//   },
// });
