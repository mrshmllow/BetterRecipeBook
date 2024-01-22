{
  description = "BRB Dev Shell";
  inputs.nixpkgs.url = "github:NixOS/nixpkgs/nixpkgs-unstable";
  inputs.flake-utils.url = "github:numtide/flake-utils";

  outputs = {
    nixpkgs,
    flake-utils,
    ...
  }:
    flake-utils.lib.eachDefaultSystem (system: let
      pkgs = nixpkgs.legacyPackages.${system};
    in {
      devShells.default = pkgs.mkShell {
        packages = with pkgs; [
          bashInteractive
          libglvnd
          openjdk17
        ];
        # https://frnks.top/2022/10/13/2022-10-14-linux_minecraft%E6%97%A0%E6%B3%95%E5%88%9D%E5%A7%8B%E5%8C%96openal%20/#:~:text=%E7%AC%94%E8%80%85%E6%9C%80%E8%BF%91%E7%BC%96%E5%86%99Mod%E8%80%8CIDEA%E7%9A%84%E5%BA%94%E7%94%A8%E7%A8%8B%E5%BA%8F%E6%9E%84%E5%BB%BA%E9%85%8D%E7%BD%AE%E6%B2%A1%E6%9C%89Wrapper%20Command%EF%BC%8C%E5%AF%BC%E8%87%B4%E5%8E%9F%E6%9D%A5%E4%BD%BF%E7%94%A8%E7%9A%84%20aoss%20%E6%96%B9%E6%B3%95%E6%97%A0%E6%95%88%E4%BA%86%EF%BC%8C%E6%89%80%E4%BB%A5%E6%9F%A5%E4%BA%86%E4%B8%80%E4%B8%8B%20aoss%20%E7%9A%84%E5%8E%9F%E7%90%86%E3%80%82%E5%8E%9F%E6%9D%A5%E5%B0%B1%E6%98%AF%E6%8A%8A%20LD_PRELOAD%20%E7%8E%AF%E5%A2%83%E5%8F%98%E9%87%8F%E5%8A%A0%E4%B8%8A%20alsa%2Doss%20%E5%8C%85%E4%B8%AD%E7%9A%84%20lib/libaoss.so%EF%BC%8C%E4%BA%8E%E6%98%AF%E4%B9%8E%E7%9B%B4%E6%8E%A5%E5%9C%A8%E6%9E%84%E5%BB%BA%E9%85%8D%E7%BD%AE%E4%B8%AD%E7%9A%84%E7%8E%AF%E5%A2%83%E5%8F%98%E9%87%8F%E5%8A%A0%E4%B8%8A%E8%BF%99%E4%B8%AA%E5%BA%93%E5%8D%B3%E5%8F%AF%EF%BC%88%E5%89%8D%E6%8F%90%E6%98%AF%E5%AE%89%E8%A3%85%E4%BA%86%20alsa%2Doss%20%E5%8C%85%EF%BC%89%E3%80%82
        LD_PRELOAD = "${pkgs.alsa-oss}/$LIB/libaoss.so";
        shellHook = ''
            export LD_LIBRARY_PATH="${pkgs.flite}/lib:$LD_LIBRARY_PATH"
        '';
      };
    });
}
