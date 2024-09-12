package com.example.demo.tournaments;

import com.example.demo.mapper.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class TournamentMapperImpl implements Mapper<TournamentEntity, TournamentDto>{

    private ModelMapper modelMapper;

    public TournamentMapperImpl(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }

    @Override
    public TournamentDto mapTo(TournamentEntity tournamentEntity){
        return modelMapper.map(tournamentEntity, TournamentDto.class);
    }

    @Override
    public TournamentEntity mapFrom(TournamentDto tournamentDto){
        return modelMapper.map(tournamentDto, TournamentEntity.class);
        
    }
}
