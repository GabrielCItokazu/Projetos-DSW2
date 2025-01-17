package br.ufscar.dc.dsw.controller;

import java.util.List;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.ufscar.dc.dsw.domain.Hotel;
import br.ufscar.dc.dsw.domain.PromoHotel;
import br.ufscar.dc.dsw.domain.SiteReserva;
import br.ufscar.dc.dsw.service.spec.IPromoHotelService;
import br.ufscar.dc.dsw.service.spec.ISiteReservaService;

@Controller
@RequestMapping("/sites")
public class AdminSiteReservaController {

	@Autowired
	private ISiteReservaService service;
	
	@Autowired
	private IPromoHotelService serviceP;
	
	@Autowired
	private BCryptPasswordEncoder encoder;

	@GetMapping("/cadastrar")
	public String cadastrar(SiteReserva siteReserva) {
		return "admin/sitereserva/cadastro";
	}

	@GetMapping("/listar")
	public String listar(ModelMap model) {
		model.addAttribute("sites", service.buscarTodos());
		return "admin/sitereserva/lista";
	}

	@PostMapping("/salvar")
	public String salvar(@Valid SiteReserva siteReserva, BindingResult result, RedirectAttributes attr) {
		
		if (result.hasErrors()) {
			System.out.println(result);
			return "admin/sitereserva/cadastro";
		}
		
		siteReserva.setSenha(encoder.encode(siteReserva.getSenha()));
		service.salvar(siteReserva);
		attr.addFlashAttribute("sucess", "Site de Reserva inserido com sucesso");
		return "redirect:/sites/listar";
	}

	@GetMapping("/editar/{id}")
	public String preEditar(@PathVariable("id") Long id, ModelMap model) {
		model.addAttribute("siteReserva", service.buscarPorId(id));
		return "admin/sitereserva/cadastro";
	}

	@PostMapping("/editar")
	public String editar(@Valid SiteReserva siteReserva, BindingResult result, RedirectAttributes attr) {

		if (result.hasErrors()) {
			return "admin/sitereserva/cadastro";
		}
		siteReserva.setSenha(encoder.encode(siteReserva.getSenha()));
		service.salvar(siteReserva);
		attr.addFlashAttribute("sucess", "Site de Reserva editado com sucesso.");
		return "redirect:/sites/listar";
	}

	@GetMapping("/excluir/{id}")
	public String excluir(@PathVariable("id") Long id, ModelMap model) {
		SiteReserva siteReserva = service.buscarPorId(id);
		List<PromoHotel> promosite = serviceP.findBySitereserva(siteReserva);
		
		if (promosite.isEmpty()) {
			service.excluir(id);
			model.addAttribute("sucess", "Site de Reserva excluído com sucesso.");
		} else {
			model.addAttribute("fail", "Site de Reserva não excluído. Possui promo(s) vinculada(s).");
		}
		return listar(model);
	}
}