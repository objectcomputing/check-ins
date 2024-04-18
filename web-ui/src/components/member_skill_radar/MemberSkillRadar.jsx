import React, {useEffect, useState} from "react";
import PropTypes from "prop-types";

import {Radar, RadarChart, PolarGrid, Legend, PolarAngleAxis, PolarRadiusAxis, ResponsiveContainer, Tooltip} from "recharts";
import {RADAR_COLORS} from "./RadarColors.jsx";

const propTypes = {
  data: PropTypes.arrayOf(PropTypes.shape({
    skill: PropTypes.string.isRequired
  })),
  members: PropTypes.arrayOf(PropTypes.object).isRequired
};

const MemberSkillRadar = ({ data, members }) => {

  const [colors, setColors] = useState({});  // Maps member id to object of colors for radar
  const [colorOptions, setColorOptions] = useState(RADAR_COLORS);  // Collection of predefined colors to choose from

  // Generate a random color using HSL
  const generateColor = () => {
    const hue = Math.random() * 360;
    return {
      fill: `hsl(${hue}, 100%, 50%)`,
      stroke: `hsl(${hue}, 100%, 30%)`  // Use a darker version of the same color
    };
  }

  // Pick a random color from the predefined colors
  const pickColor = (options) => {
    const colorKeys = Object.keys(options);
    return colorKeys[colorKeys.length * Math.random() << 0];
  }

  useEffect(() => {
    const updatedColors = {...colors};
    const updatedColorOptions = {...colorOptions};
    members.forEach((member) => {
      // Only get a new color if the member doesn't have one already
      if (!(member.name in colors)) {
        // Use predefined colors until all those colors have been used
        if (Object.keys(updatedColorOptions).length > 0) {
          const colorName = pickColor(updatedColorOptions);
          const selectedColor = updatedColorOptions[colorName];
          updatedColors[member.name] = {
            fill: selectedColor.light,
            stroke: selectedColor.dark
          };
          delete updatedColorOptions[colorName];
          console.log(`Used color: ${colorName} - ${Object.keys(updatedColorOptions).length} remaining`);
        } else {
          updatedColors[member.name] = generateColor();
          console.log(updatedColors[member.name]);
        }
      }
    });
    setColors(updatedColors);
    setColorOptions(updatedColorOptions);
  }, [members]);

  return (
    <ResponsiveContainer width="100%" height="100%">
      <RadarChart cx="50%" cy="50%" outerRadius="80%" data={data}>
        <PolarGrid/>
        <PolarAngleAxis dataKey="skill"/>
        <PolarRadiusAxis domain={[0, 5]}/>
        <Tooltip/>
        {members?.map((member) => (
          <Radar
            key={member.name}
            name={member.name}
            dataKey={member.name}
            fill={colors[member.name] && colors[member.name].fill}
            stroke={colors[member.name] && colors[member.name].fill}
            fillOpacity={0.6}
          />
        ))}
        <Legend onClick={(t) => console.log(t)}/>
      </RadarChart>
    </ResponsiveContainer>
  );
}

MemberSkillRadar.propTypes = propTypes;

export default MemberSkillRadar;