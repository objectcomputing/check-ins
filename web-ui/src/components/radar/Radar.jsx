import React, {useEffect, useState} from "react";
import PropTypes from "prop-types";

import {Radar, RadarChart, PolarGrid, Legend, PolarAngleAxis, PolarRadiusAxis, ResponsiveContainer, Tooltip} from "recharts";

const propTypes = {
  data: PropTypes.arrayOf(PropTypes.shape({
    skill: PropTypes.string.isRequired
  })),
  members: PropTypes.arrayOf(PropTypes.object).isRequired
};

const MemberSkillRadar = ({ data, members }) => {

  const [colors, setColors] = useState({});

  // Calculates a pair of colors for the fill and stroke
  const calculateColors = () => {
    const hue = Math.random() * 360;
    return {
      fill: `hsl(${hue}, 100%, 50%)`,
      stroke: `hsl(${hue}, 100%, 30%)`  // Use a darker version of the same color
    };
  }

  useEffect(() => {
    const updatedColors = {...colors};
    members.forEach((member) => {
      if (!(member.name in colors)) {
        updatedColors[member.name] = calculateColors();
      }
    });
    setColors(updatedColors);
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
            stroke={colors[member.name] && colors[member.name].stroke}
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