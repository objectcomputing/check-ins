import React from "react";
import './Sidebar.css';
import RightSidebar from "./RightSidebar";
const SidebarCard = (props) => {

    const menuList = [
         {
            index: 0,
            name: "Culture Video",
            title: "Lorem ipsum",
            completed: false,
            child: []
         },
         {
            index: 1,
            name: "About You Survey",
            title: "Lorem ipsum",
            completed: false,
            child: []
         },
         {
            index: 2,
            name: "Work Preference",
            title: "Lorem ipsum",
            completed: false,
            child: []
         },
         {
            index: 3,
            name: "Computer and Accessories",
            title: "Lorem ipsum",
            completed: false,
            child: []
         },
         {
            index: 4,
            name: "Internal Document Signing",
            title: "Lorem ipsum",
            completed: false,
            child: []
         },
         {
            index: 5,
            name: "Check-Ins Skills",
            title: "Lorem ipsum",
            completed: false,
            child: []
         },
         {
            index: 6,
            name: "Cake!",
            title: "Lorem ipsum",
            completed: false,
            child: []
         }
    ]
  return (
    <div className="sidebar_card">
        <RightSidebar menuList={menuList}/>
    </div>
  )
};

export default SidebarCard;
