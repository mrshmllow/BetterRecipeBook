{
  description = "BRB Dev Shell";
  inputs.nixpkgs.url = "github:NixOS/nixpkgs/nixpkgs-unstable";
  inputs.devenv.url = "github:cachix/devenv";
  inputs.nix2container.url = "github:nlewo/nix2container";
  inputs.nix2container.inputs = {nixpkgs.follows = "nixpkgs";};
  inputs.mk-shell-bin.url = "github:rrbutani/nix-mk-shell-bin";

  outputs = inputs @ {
    nixpkgs,
    flake-parts,
    ...
  }:
    flake-parts.lib.mkFlake {inherit inputs;} {
      imports = [
        inputs.devenv.flakeModule
      ];
      systems = nixpkgs.lib.systems.flakeExposed;

      perSystem = {
        config,
        self',
        inputs',
        pkgs,
        system,
        ...
      }: {
        devenv.shells.default = {
          packages = with pkgs; [
            libglvnd
          ];

          languages.java.enable = true;
          languages.java.gradle.enable = true;
          languages.java.jdk.package = pkgs.jdk21;

          env.LD_PRELOAD = "${pkgs.alsa-oss}/$LIB/libaoss.so";
          # https://frnks.top/2022/10/13/2022-10-14-linux_minecraft%E6%97%A0%E6%B3%95%E5%88%9D%E5%A7%8B%E5%8C%96openal%20/
          env.LD_LIBRARY_PATH = "${pkgs.flite}/lib:${pkgs.libglvnd}/lib:${pkgs.udev}/lib:$LD_LIBRARY_PATH";
        };
      };
    };
}
