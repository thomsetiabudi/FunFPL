package com.thomas.personal.fpl.funfpl.bl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.thomas.personal.fpl.funfpl.model.LeagueDto;
import com.thomas.personal.fpl.funfpl.model.LeagueGwStandingsDataDto;
import com.thomas.personal.fpl.funfpl.persistence.TblEvent;
import com.thomas.personal.fpl.funfpl.persistence.TblLeague;
import com.thomas.personal.fpl.funfpl.persistence.TblLeagueGwStandings;
import com.thomas.personal.fpl.funfpl.persistence.TblPlayer;
import com.thomas.personal.fpl.funfpl.repository.EventRepository;
import com.thomas.personal.fpl.funfpl.repository.LeagueGwStandingsRepository;
import com.thomas.personal.fpl.funfpl.repository.LeaguePlayerRepository;
import com.thomas.personal.fpl.funfpl.repository.LeagueRepository;
import com.thomas.personal.fpl.funfpl.repository.PlayerEventRepository;
import com.thomas.personal.fpl.funfpl.repository.PlayerRepository;

@Component
public class DataViewBl {

	private PlayerRepository playerRepository;

	private LeagueRepository leagueRepository;

	private LeaguePlayerRepository leaguePlayerRepository;

	private PlayerEventRepository playerEventRepository;

	private EventRepository eventRepository;

	private LeagueGwStandingsRepository leagueGwStandingsRepository;

	@Autowired
	public DataViewBl(PlayerRepository playerRepository, LeagueRepository leagueRepository,
			LeaguePlayerRepository leaguePlayerRepository, PlayerEventRepository playerEventRepository,
			EventRepository eventRepository, LeagueGwStandingsRepository leagueGwStandingsRepository) {
		this.playerRepository = playerRepository;
		this.leagueRepository = leagueRepository;
		this.leaguePlayerRepository = leaguePlayerRepository;
		this.playerEventRepository = playerEventRepository;
		this.eventRepository = eventRepository;
		this.leagueGwStandingsRepository = leagueGwStandingsRepository;
	}

	public List<LeagueDto> getLeagueList() {
		List<LeagueDto> result = new ArrayList<>();

		Iterable<TblLeague> leagueList = leagueRepository.findAll();
		for (TblLeague tblLeague : leagueList) {
			LeagueDto leagueData = new LeagueDto();
			leagueData.setId(tblLeague.getId());
			leagueData.setName(tblLeague.getName());
			result.add(leagueData);
		}

		return result;

	}

	public List<Long> getAvailableGwStandings() {
		List<Long> result = new ArrayList<>();

		List<TblEvent> availableEventList = eventRepository.findByStatusOrderByEventDesc("FINISH");

		for (TblEvent tblEvent : availableEventList) {
			result.add(tblEvent.getEvent());
		}

		return result;
	}

	public List<LeagueGwStandingsDataDto> getLeagueGwStandings(Long leagueId, Long event) {
		List<LeagueGwStandingsDataDto> result = new ArrayList<>();
		Optional<TblLeague> league = leagueRepository.findById(leagueId);

		if (!league.isPresent()) {
			return result;
		}

		List<TblLeagueGwStandings> leagueGwStandingsList = leagueGwStandingsRepository
				.findByLeagueIdAndEventIdOrderByPlayerGwStandingsOrderAsc(leagueId, event);

		for (TblLeagueGwStandings tblLeagueGwStandings : leagueGwStandingsList) {
			LeagueGwStandingsDataDto leagueGwStandingData = new LeagueGwStandingsDataDto();

			Optional<TblPlayer> player = playerRepository.findById(tblLeagueGwStandings.getPlayerEntryId());
			if (!player.isPresent()) {
				continue;
			}

			leagueGwStandingData.setLeagueId(leagueId);
			leagueGwStandingData.setLeagueName(league.get().getName());
			leagueGwStandingData.setPlayerEntryId(tblLeagueGwStandings.getPlayerEntryId());
			leagueGwStandingData.setPlayerEntryName(player.get().getEntryName());
			leagueGwStandingData.setPlayerEventScore(tblLeagueGwStandings.getPlayerEventScore());
			leagueGwStandingData.setPlayerName(player.get().getPlayerName());
			leagueGwStandingData.setPlayerNick(player.get().getPlayerNick());
			leagueGwStandingData.setPlayerPositionGain(tblLeagueGwStandings.getPlayerGwStandingsPositionGain());
			leagueGwStandingData.setPlayerPrevStandingsOrder(tblLeagueGwStandings.getPlayerPrevGwStandingsOrder());
			leagueGwStandingData.setPlayerStandingsOrder(tblLeagueGwStandings.getPlayerGwStandingsOrder());
			leagueGwStandingData.setPlayerTotalScore(tblLeagueGwStandings.getPlayerTotalScore());

			result.add(leagueGwStandingData);
		}

		return result;
	}

}