import React from "react";

import {Radar, RadarChart, PolarGrid, Legend, PolarAngleAxis, PolarRadiusAxis, ResponsiveContainer} from "recharts";

const MemberSkillRadar = ({data, selectedMembers}) => (
  <ResponsiveContainer width="100%" height="100%">
    <RadarChart cx="50%" cy="50%" outerRadius="80%" data={data}>
      <PolarGrid/>
      <PolarAngleAxis dataKey="skill"/>
      <PolarRadiusAxis domain={[0, 5]}/>
      {selectedMembers.map((member) => (
        <Radar
          name={member.name}
          dataKey={member.name}
          fill={member.color}
          fillOpacity={0.6}
        />
      ))}
      <Legend/>
    </RadarChart>
  </ResponsiveContainer>
)

export default MemberSkillRadar;