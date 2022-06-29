import React from "react";
import SidebarItem from "./SidebarItem";
import './SidebarComponents.css';
const SidebarComponents = (props) => {

  return (
    <ul className="sidebar_menu">
      {props.items.map((item) => (
        <SidebarItem
          key={item.id}
          title={item.title}
          completed={item.completed}
          children={item.children}
        />
      ))}
    </ul>
  );
};

export default SidebarComponents;
