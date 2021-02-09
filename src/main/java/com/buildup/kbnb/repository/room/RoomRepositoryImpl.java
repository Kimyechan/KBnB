package com.buildup.kbnb.repository.room;

import com.buildup.kbnb.dto.room.search.*;
import com.buildup.kbnb.model.QReservationDate;
import com.buildup.kbnb.model.room.BedRoom;
import com.buildup.kbnb.model.room.Room;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.util.List;

import static com.buildup.kbnb.model.QLocation.location;
import static com.buildup.kbnb.model.room.QRoom.room;
import static com.buildup.kbnb.model.QReservationDate.reservationDate;

public class RoomRepositoryImpl implements RoomRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public RoomRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Room> searchByCondition(RoomSearchCondition condition, Pageable pageable) {
        List<Room> content = queryFactory
                .selectFrom(room).distinct()
                .join(room.location, location).fetchJoin()
                .where(roomTypeEq(condition.getRoomType()),
                        costBetween(condition.getCostSearch()),
                        latitudeBetween(condition.getLocationSearch()),
                        longitudeBetween(condition.getLocationSearch()),
                        guestNumCheck(condition.getGuestSearch()),
                        bedRoomNumGreaterThan(condition.getBedRoomNum()),
                        bathRoomNumGreaterThan(condition.getBathRoomNum()),
                        bedNumGreaterThan(condition.getBedNum()),
                        dateBetween(condition.getCheckDateSearch(), room.id))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .selectFrom(room).distinct()
                .where(roomTypeEq(condition.getRoomType()),
                        costBetween(condition.getCostSearch()),
                        latitudeBetween(condition.getLocationSearch()),
                        longitudeBetween(condition.getLocationSearch()),
                        guestNumCheck(condition.getGuestSearch()),
                        bedRoomNumGreaterThan(condition.getBedRoomNum()),
                        bathRoomNumGreaterThan(condition.getBathRoomNum()),
                        bedNumGreaterThan(condition.getBedNum()))
                .fetchCount();

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression bathRoomNumGreaterThan(Integer bathRoomNum) {
        return bathRoomNum == null ? null : room.bathRoomList.size().goe(bathRoomNum);
    }

    private BooleanExpression bedRoomNumGreaterThan(Integer bedRoomNum) {
        return bedRoomNum == null ? null : room.bedRoomList.size().goe(bedRoomNum);
    }

    private BooleanExpression bedNumGreaterThan(Integer bedNum) {
        return bedNum == null ? null : room.bedNum.goe(bedNum);
    }

    private BooleanExpression roomTypeEq(String roomType) {
        return roomType == null ? null : room.roomType.eq(roomType);
    }

    private BooleanExpression costBetween(CostSearch costSearch) {
//        return costSearch.equals(new CostSearch())? null : room.roomCost.between(costSearch.getMinCost(), costSearch.getMaxCost());
        return costSearch == null || costSearch.equals(new CostSearch())
                ? null : room.roomCost.between(costSearch.getMinCost(), costSearch.getMaxCost());
    }

    private BooleanExpression latitudeBetween(LocationSearch locationSearch) {
        return locationSearch == null || locationSearch.equals(new LocationSearch())
                ? null : room.location.latitude.between(locationSearch.getLatitudeMin(), locationSearch.getLatitudeMax());
    }

    private BooleanExpression longitudeBetween(LocationSearch locationSearch) {
        return locationSearch == null || locationSearch.equals(new LocationSearch())
                ? null : room.location.longitude.between(locationSearch.getLongitudeMin(), locationSearch.getLongitudeMax());
    }

    private BooleanExpression guestNumCheck(GuestSearch guestSearch) {
        return guestSearch == null || guestSearch.equals(new GuestSearch())
                ? null : room.peopleLimit.goe(guestSearch.getNumOfAdult() + guestSearch.getNumOfKid());
    }

    private BooleanExpression dateBetween(CheckDateSearch checkDateSearch, NumberPath<Long> id) {
        return checkDateSearch.equals(new CheckDateSearch()) || checkDateSearch.equals(new CheckDateSearch())
                ? null :
                JPAExpressions.select(reservationDate.date)
                        .from(reservationDate)
                        .where(reservationDate.room.id.eq(id)
                                .and(reservationDate.date.between(Expressions.asDate(checkDateSearch.getStartDate())
                                        , Expressions.asDate(checkDateSearch.getEndDate())))).notExists();
//                Expressions.asDate(checkDateSearch.getStartDate()).notIn(
//                        JPAExpressions.select(reservationDate.date)
//                                .from(reservationDate)
//                                .where(reservationDate.room.id.eq(id)))
//                        .and(
//                                Expressions.asDate(checkDateSearch.getEndDate()).goeAny(
//                                        JPAExpressions.select(reservationDate.date)
//                                                .from(reservationDate)
//                                                .where(reservationDate.room.id.eq(id)))
//                        );
    }
}
