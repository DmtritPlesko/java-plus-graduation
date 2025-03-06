package interaction.controller;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "category")
public interface FeignCategoryController {


}
