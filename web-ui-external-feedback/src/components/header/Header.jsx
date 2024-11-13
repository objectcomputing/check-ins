import React from 'react';
import './Header.css';

const Header = ({ title, children }) => {
  return (
    <div className="header">
      {children}
      <h1>{title}</h1>
    </div>
  );
};

export default Header;
