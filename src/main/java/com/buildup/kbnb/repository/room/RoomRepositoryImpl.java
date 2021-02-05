package com.buildup.kbnb.repository.room;

import com.buildup.kbnb.dto.room.search.CostSearch;
import com.buildup.kbnb.dto.room.search.GuestSearch;
import com.buildup.kbnb.dto.room.search.LocationSearch;
import com.buildup.kbnb.dto.room.search.RoomSearchCondition;
import com.buildup.kbnb.model.room.Room;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.util.List;

import static com.buildup.kbnb.model.QLocation.location;
import static com.buildup.kbnb.model.room.QRoom.room;

public class RoomRepositoryImpl implements RoomRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public RoomRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Room> searchByCondition(RoomSearchCondition condition, Pageable pageable) {
        // ToDo: 침대수, 침실수, 욕실수 필터링
        // ToDo: Sorting 기준??

        List<Room> content = queryFactory
                .selectFrom(room).distinct()
                .join(room.location, location).fetchJoin()
                .where(roomTypeEq(condition.getRoomType()),
                        costBetween(condition.getCostSearch()),
                        latitudeBetween(condition.getLocationSearch()),
                        longitudeBetween(condition.getLocationSearch()),
                        guestNumCheck(condition.getGuestSearch()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .selectFrom(room).distinct()
                .where(roomTypeEq(condition.getRoomType()),
                        costBetween(condition.getCostSearch()),
                        latitudeBetween(condition.getLocationSearch()),
                        longitudeBetween(condition.getLocationSearch()),
                        guestNumCheck(condition.getGuestSearch()))
                .fetchCount();

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression roomTypeEq(String roomType) {
        return roomType == null ? null : room.roomType.eq(roomType);
    }

    private BooleanExpression costBetween(CostSearch costSearch) {
        return costSearch == null ? null : room.roomCost.between(costSearch.getMinCost(), costSearch.getMaxCost());
    }

    private BooleanExpression latitudeBetween(LocationSearch locationSearch) {
        return locationSearch == null ? null : room.location.latitude.between(locationSearch.getLatitudeMin(), locationSearch.getLatitudeMax());
    }

    private BooleanExpression longitudeBetween(LocationSearch locationSearch) {
        return locationSearch == null ? null : room.location.longitude.between(locationSearch.getLongitudeMin(), locationSearch.getLongitudeMax());
    }

    private BooleanExpression guestNumCheck(GuestSearch guestSearch) {
        return guestSearch == null ? null : room.peopleLimit.goe(guestSearch.getNumOfAdult() + guestSearch.getNumOfKid());
    }
}
