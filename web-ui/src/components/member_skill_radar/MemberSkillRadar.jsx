import React, {useEffect, useState} from "react";
import PropTypes from "prop-types";

import {
  Legend,
  PolarAngleAxis,
  PolarGrid,
  PolarRadiusAxis,
  Radar,
  RadarChart,
  ResponsiveContainer,
  Tooltip
} from "recharts";
import {RADAR_COLORS} from "./RadarColors.jsx";

const propTypes = {
  data: PropTypes.arrayOf(PropTypes.shape({
    skill: PropTypes.string.isRequired
  })),
  members: PropTypes.arrayOf(PropTypes.object).isRequired
};

const MemberSkillRadar = ({ data, members }) => {

  const [colors, setColors] = useState({});  // Maps member id to object of colors for radar
  const [colorOptions, setColorOptions] = useState(RADAR_COLORS);  // List of predefined colors to choose from

  useEffect(() => {
    const updatedColors = {...colors};
    const updatedColorOptions = [...colorOptions];
    members.forEach((member) => {
      // Only get a new color if the member doesn't have one already
      if (!(member.name in colors)) {
        // Use predefined colors until all those colors have been used
        if (updatedColorOptions.length > 0) {
          // Choose a random color from the list
          const colorIndex = Math.floor(Math.random() * updatedColorOptions.length);
          updatedColors[member.name] = updatedColorOptions[colorIndex];
          updatedColorOptions.splice(colorIndex, 1);  // Prevent using this color again
        } else {
          // Generate a random color using HSL
          const hue = Math.random() * 360;
          updatedColors[member.name] = `hsl(${hue}, 100%, 50%)`;
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
            fill={colors[member.name]}
            stroke={colors[member.name]}
            fillOpacity={0.6}
          />
        ))}
        <Legend/>
      </RadarChart>
    </ResponsiveContainer>
  );
}

MemberSkillRadar.propTypes = propTypes;

export default MemberSkillRadar;