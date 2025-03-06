package interaction.controller;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "stat")
public interface FeignStatController {



}
