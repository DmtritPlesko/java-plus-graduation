package ewm.subscription.mapper;

import ewm.subscription.dto.SubscriptionDto;
import ewm.subscription.model.Subscription;
import ewm.user.dto.UserDto;
import ewm.user.mappers.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface SubscriptionMapper {
    SubscriptionDto toSubscriptionDto(Subscription subscription);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "subscription.follower", source = "follower")
    @Mapping(target = "subscription.following", source = "following")
    Subscription toSubscription(@MappingTarget Subscription subscription, UserDto follower, UserDto following);
}
