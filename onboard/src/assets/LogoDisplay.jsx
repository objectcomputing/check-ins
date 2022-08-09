import React from 'react';
import ociLogo from './img/ocicube-white-50%smaller.png';
import styled from 'styled-components';

const IMG = styled.img`
  width: 100%;
  background-image: url(${ociLogo});
  background-position: center;
  background-color:black;
  background-repeat: no-repeat;
  background-size: contain;
  border: px #0095ff solid;
  border-radius: 50%;
  padding-left: 100px;
  padding-bottom: 100px;
`;

const LogoDisplay = ({ width, height }) => {
  let maxWidth = width ? width : '100%';
  let fullHeight = height ? height : '100%';

  return <IMG style={{ position: "relative", maxWidth: "75px", height: "75px" }} />;
};

export default LogoDisplay;
