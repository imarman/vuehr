package org.newbie.vhr.service;

import org.newbie.vhr.model.Nation;
import org.newbie.vhr.mapper.NationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NationService {

    @Autowired
    private NationMapper nationMapper;

    public List<Nation> getAllNation() {
        return nationMapper.getAllNation();
    }
}
